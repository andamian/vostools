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

from vos import vos
from .version import version
from vos.commonparser import CommonParser, set_logging_level_from_args


def mountvofs():
    parser = CommonParser(description='mount vospace as a filesystem.')

    # mountvofs specific options
    parser.add_option("--vospace", help="the VOSpace to mount", default="vos:")
    parser.add_option("--mountpoint",
                      help="the mountpoint on the local filesystem",
                      default="/tmp/vospace")
    parser.add_option(
        "-f", "--foreground", action="store_true",
        help="Mount the filesystem as a foreground opperation and " +
        "produce copious amounts of debuging information")
    parser.add_option("--allow_other", action="store_true", default=False,
                      help="Allow all users access to this mountpoint")
    parser.add_option("--log", action="store",
                      help="File to store debug log to",
                      default="/tmp/vos.err")
    parser.add_option(
        "--nothreads",
        help="Only run in a single thread",
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
    if not os.access(opt.certfile, os.F_OK):
        # setting this to 'blank' instead of None since 'None' implies use
        # the default.
        certfile = ""
    else:
        certfile = os.path.abspath(opt.certfile)

    cmd = ['sshfs', '-o', 'defer_permissions']

    conn = vos.Connection(vospace_certfile=certfile, vospace_token=opt.token)

    # negotiate the transfer and get the mount target

    target = 'root@localhost:/tmp'
    port = '-p 2222'

    cmd = ['sshfs']
    ops = [] # operational flags
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
    cmd.append('-C') # use compression

    # keep alive options
    #ops.append('reconnect,ServerAliveInterval=15,ServerAliveCountMax=3')

    # now add the operational options
    cmd.append('-o')
    cmd.append(','.join([str(i) for i in ops]))

    logger.debug('Executing command: {}'.format(' '.join(cmd)))

    # create mount directory if it doesn't exist
    if not os.path.exists(opt.mountpoint):
        logger.debug('Create the mount directory {}'.format(opt.mountpoint))
        os.makedirs(opt.mountpoint)
    p = subprocess.call(cmd, stdout=subprocess.PIPE)
    if p:
        print('ERROR')
        sys.exit(p)
    else:
        print('DONE')