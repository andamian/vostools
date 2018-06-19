#!python
"""A FUSE based filesystem view of VOSpace."""
from __future__ import (absolute_import, division, print_function,
                        unicode_literals)
import os
import logging
import getpass
import subprocess
import sys
from sys import platform
from builtins import str
from vos import vos
from .version import version
from vos.commonparser import CommonParser, set_logging_level_from_args


def mountvofs():
    parser = CommonParser(description='mount vospace as a filesystem.')

    # mountvofs specific options
    parser.add_option("vospace", help="the VOSpace to mount", default="vos:")
    parser.add_option("mountpoint",
                      help="the mountpoint on the local filesystem",
                      default="/tmp/vospace")
    parser.add_option("--log", action="store",
                      help="File to store debug log to",
                      default="/tmp/vos.err")
    parser.add_option(
        "--nothreads",
        help="Only run in a single thread, causes some blocking.",
        action="store_true")
    opt = parser.parse_args()
    set_logging_level_from_args(opt)

    log_format = ("%(asctime)s %(thread)d vos-" + str(version) +
                  " %(module)s.%(funcName)s.%(lineno)d %(message)s")

    username = getpass.getuser()  # not to be used for access control
    lf = logging.Formatter(fmt=log_format)
    fh = logging.FileHandler(
        filename=os.path.abspath('/tmp/vos.{}.exceptions'.format(username)))
    fh.formatter = lf

    # send the 'logException' statements to a seperate log file.
    logger = logging.getLogger('exceptions')
    logger.handlers = []
    logger.setLevel(logging.ERROR)
    logger.addHandler(fh)

    fh = logging.FileHandler(filename=os.path.abspath(opt.log))
    fh.formatter = lf
    logger = logging.getLogger('vofs')
    logger.handlers = []
    logger.setLevel(opt.log_level)
    logger.addHandler(fh)

    vos_logger = logging.getLogger('vos')
    vos_logger.handlers = []
    vos_logger.setLevel(logging.ERROR)
    vos_logger.addHandler(fh)

    logger.debug("Checking connection to VOSpace ")

    client = vos.Client(vospace_certfile=opt.certfile,
                        vospace_token=opt.token)
    # TODO resourceID hardcoded for now but a scheme that supports
    # multiple vospaces is needed.
    client.conn.resourceID = 'ivo://canfar.net/cavern'
    # negotiate the transfer and get the mount target
    target = client.get_node_url(opt.vospace, method='MOUNT',
                                 full_negotiation=True)
    assert len(target) > 0, 'No sshfs target found'
    parts = target[0].split(':')
    assert len(parts) == 4,\
        'Do not know how to parse target {}.'.format(target)
    port = '-p {}'.format(parts[2])
    target = '{}:{}'.format(parts[1], parts[3])  # only part 1 and 3 needed
    cmd = ['sshfs']
    ops = []  # operational flags
    if platform == "darwin":
        # forward the decision whether to allow or deny an operation to the
        # target file system
        ops.append('defer_permissions')
    cmd.append(target)
    cmd.append(opt.mountpoint)
    cmd.append(port)
    if opt.debug:
        ops.append('sshfs_debug')
    if opt.nothreads:
        cmd.append('-s')
    cmd.append('-C')  # use compression
    # This is something that could potentially be improved in production
    # Password is received through stdin but that makes it impossible to
    # have the user confirm the authenticity of the ECDSA key (which
    # currently changes every time the cavern-sshd images is rebuilt)
    # so we need to bypass the acceptance step.
    ops.append('password_stdin')
    ops.append('StrictHostKeyChecking=no')

    # keep alive options
    ops.append('reconnect')
    ops.append('ServerAliveInterval=15')
    ops.append('ServerAliveCountMax=3')

    # now add the operational options
    cmd.append('-o')
    cmd.append(','.join([str(i) for i in ops]))

    logger.debug('Executing command: {}'.format(' '.join(cmd)))

    # create mount directory if it doesn't exist
    if not os.path.exists(opt.mountpoint):
        logger.debug('Create the mount directory {}'.format(opt.mountpoint))
        os.makedirs(opt.mountpoint)
    p = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE)
    paswd = getpass.getpass(parts[1])
    out, err = p.communicate(bytes(paswd, 'UTF-8'))

    if p.returncode:

        sys.stderr.write(err.decode('UTF-8'))
        sys.stderr.write('ERROR\n')
        # TODO Following code unmounts directory since sshfs does not do it
        # While convenient, not sure it's the right thing to do since
        # the error might be due to the directory being already mounted
        # in which case we probably shouldn't unmount it.
        # sys.stderr.write('Unmount the point {}\n'.format(opt.mountpoint))
        # if platform == 'darwin':
        #     unmount = ['diskutil', 'unmount']
        # else:
        #     unmount = ['umount']
        # unmount.append(opt.mountpoint)
        # logger.debug('Executing command {}'.format(' '.join(unmount)))
        # p2 = subprocess.Popen(unmount, stdout=subprocess.PIPE)
        # p2.wait()
        # if p2.returncode:
        #     sys.stderr.write('Errors while unmounting the mount point...')
        # sys.exit(p.returncode)
    else:
        print('DONE')
