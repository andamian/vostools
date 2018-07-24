#!/bin/bash
# check environment

INST=1
FUNC="fitsverify"
DIR=-1
for i in "$@"
do
case $i in
    -i=*|--instances=*)
    INST="${i#*=}"
    shift # past argument=value
    ;;
    -f=*|--function=*)
    FUNC="${i#*=}"
    shift # past argument=value
    ;;
    -d=*|--directory=*)
    DIR="${i#*=}"
    shift # past argument=value
    ;;
    *)
        # unknown option
        echo "container arguments: [-i|--instances=[1..] | -d|--directory=[1..]] -f|--function=[fitsverify|gzip|gunzip]"
        exit -1
    ;;
esac
done

if [ "$FUNC" != "fitsverify" ] && [ "$FUNC" != "gzip" ] && [ "$FUNC" != "gunzip" ]; then
    echo "function can only be [fitsverify|gzip|gunzipa] $FUNC"
    exit -1
fi

if [ "$INST" -lt "1" ]; then
    echo "Invalid number of instances $INST (Must be > 0)"
    exit -1
fi
if [ "$DIR" -gt "-1" ]; then
    if [ "$INST" -gt "1" ]; then
        echo "-d|--directory can only be used with single instance"
        exit -1
    fi
fi

set PASS_FILE='/admin/etc/cadcregtest1.pass1'

if [ ! -f $PASS_FILE ]; then
    echo "Could not find the cadcregtest1 pass file. Is /admin mapped to correct CADC \$A?"
    exit -1
fi


VMOUNT_ROOT='/tmp/cadcregtest1/vmount'
MOUNTVOFS_ROOT='/tmp/cadcregtest1/mountvofs'
EXT_ROOT='/data'


############################## single mount point setup ######################################
S_VMOUNT=$VMOUNT_ROOT/one
S_MOUNTVOFS=$MOUNTVOFS_ROOT/one
S_EXT=$EXT_ROOT

if [ ! -d $EXT_EXT ]; then
    echo "No external volume found to test against"
    s_targets=($S_VMOUNT $S_MOUNTVOFS)
else
    s_targets=($S_VMOUNT $S_MOUNTVOFS $S_EXT)
fi

# create the single mount points
echo "create the single mount at $S_VMOUNT"
mkdir -p $S_VMOUNT
vmount --cert /admin/test-certificates/x509_CADCRegtest1.pem \
    vos:CADCRegtest1/vmount-perf-test $S_VMOUNT <<< $(cat /admin/etc/cadcregtest1.pass) || (echo "ERROR: vmount" && exit -1)

echo "create the vofs mount at $S_MOUNTVOFS"
mkdir -p $S_MOUNTVOFS
mountvofs  --cert=/admin/test-certificates/x509_CADCRegtest1.pem \
    --vospace=vos:CADCRegtest1/vmount-perf-test --mountpoint=$S_MOUNTVOFS --log=/tmp/vofs_dir.log || (echo "ERROR: mountvofs" && exit -1)


############################## multiple mount points setup ######################################
M_VMOUNT=$VMOUNT_ROOT
M_MOUNTVOFS=$MOUNTVOFS_ROOT
M_EXT=$EXT_ROOT

m_targets=($M_VMOUNT $M_MOUNTVOFS)
count=0
while [ "$count" -lt "$INST" ];
do
    echo "create the sshfs mount at $M_VMOUNT/$count to point to vmount-perf-test/$count"
    mkdir -p $M_VMOUNT/$count
    vmount --cert /admin/test-certificates/x509_CADCRegtest1.pem \
        vos:CADCRegtest1/vmount-perf-test/$count $M_VMOUNT/$count <<< $(cat /admin/etc/cadcregtest1.pass) || (echo "ERROR: vmount" && exit -1)
    #sshfs cadcregtest1@proto.canfar.net:/cadcregtest1/vmount-perf-test \
    #    /tmp/CADCRegtest1/ -p 10022 -C -o password_stdin,StrictHostKeyChecking=no\
    #<<< $(cat /admin/etc/cadcregtest1.pass)

    echo "create the vofs mount at $M_MOUNTVOFS/$count to point to vmount-perf-test/$count"
    mkdir -p $M_MOUNTVOFS/$count
    mountvofs  --cert=/admin/test-certificates/x509_CADCRegtest1.pem \
        --vospace=vos:CADCRegtest1/vmount-perf-test/$count --mountpoint=$M_MOUNTVOFS/$count --log=/tmp/vofs_dir.log || (echo "ERROR: mountvofs" && exit -1)
    let count=count+1
done

function run_test()
{
    local target=$1
    local function=$2
    
    cd $target || (echo "Cannot cd to $target directory" && exit -1)
    rm -f *.gz
    set numfiles=$(ls -1q *.fits | wc -l)
    #if [ ! "$(ls -1q *.fits | wc -l)" -eq "100" ]; then
    # echo "Expected 100 fits files in target directory $target. Found just $(ls -1q *.fits | wc -l)"
    # exit -1
    #fi

    # set the source
    src=($(ls -1q *.fits))

    start=$(date +%s)   
    for f in "${src[@]}";
    do
        #echo $f
        if [ "$function" == "fitsverify" ]; then
            fitsverify $f >& /dev/null || echo "ERROR: $function - $target/$f"
        elif [ "$function" == "gzip" ]; then
            gzip -k $f >& /dev/null || echo "ERROR: $function - $target/$f"
        elif [ "$function" == "gunzip" ]; then
            gzip -k $f >& /dev/null || gunzip -c >& /dev/null || \
                 echo "ERROR: $function - $target/$f"
        else
            echo "Unknown operation $operation"
            exit -1
        fi
    done

    end=$(date +%s)
    duration=$((end-start))
    rm -f *.gz
    echo "*** $target(${#src[@]}) files - $function: $duration"
}

echo "--- Running $INST instances on one mount ---"
for index in ${!s_targets[*]}
do
    count=0
    while [ "$count" -lt "$INST" ];
    do
        if [ "$DIR" -gt "-1" ]; then
            run_test ${s_targets[$index]}/$DIR $FUNC
        else
            run_test ${s_targets[$index]}/$count $FUNC
        fi
        let count=count+1
    done
    wait
done


if [ "$INST" -gt "1" ]; then
    echo "--- Running $INST instances on $INST mounts* ---"
    echo "* - only for vmount and mountvofs"
    count=0
    while [ "$count" -lt "$INST" ];
    do
        run_test $M_VMOUNT/$count $FUNC&
        let count=count+1
    done
    wait
    count=0
    while [ "$count" -lt "$INST" ];
    do
        run_test $M_MOUNTVOFS/$count $FUNC&
        let count=count+1
    done
    wait
fi

