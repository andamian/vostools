FROM python:3.6

# install sshfs
RUN apt-get update && apt-get install -y sshfs fitsverify parallel

WORKDIR /app

# determine which version of vofs to install (same can be applied for the vos library)
# released version:
# RUN pip install vofs
# git official version
# RUN pip install -e 'git+https://github.com/opencadc/vostools'
# git fork/branch version
RUN pip install 'git+https://github.com/andamian/vostools@s2294#egg=vostools&subdirectory=vos'
RUN pip install 'git+https://github.com/andamian/vostools@s2294#egg=vostools&subdirectory=vofs'
# local copy
# RUN pip install -e .

COPY run.sh .


ENTRYPOINT ["/app/run.sh"]
