#!/bin/bash

START_DIR=`pwd`

if [ ! -e ~/.science-portal.rc -a "x$OSG_DATA" != "x" ]; then
    # we are on a OSG site which does not have a .science-portal.rc - let's
    # install a generic OSG one
    cat<<EOF >~/.science-portal.rc
#!/bin/bash

export GLIDEINS_DIR=\$OSG_DATA/engage/tmp/scienceportal/glideins
export GLIDEINS_WORK_DIR=\$OSG_WN_TMP/science-portal

export DATABASE_DIR=\$OSG_DATA/engage/scienceportal/databases
export APPLICATION_DIR=\$OSG_DATA/engage/scienceportal/apps

. \$APPLICATION_DIR/setup.sh

EOF
fi

. ~/.science-portal.rc

if [ "x$GLIDEINS_WORK_DIR" = "x" ]; then
    echo "GLIDEINS_WORK_DIR is not set in .science-portal.rc"
    exit 1                                                   
fi                                                           

WORK_DIR="$GLIDEINS_WORK_DIR/maintenance.$$"

echo
echo "Running on:" `hostname -f`

echo
echo "Started in $START_DIR"
echo "WORK_DIR is $WORK_DIR"

rm -rf $WORK_DIR
mkdir -p $WORK_DIR || {
    WORK_DIR=$START_DIR
    echo "$PATH"
}

cd $WORK_DIR || {
    echo "Unable to cd to $WORK_DIR. Exiting..."
    exit 1                                      
}                              

#######################################################################
#
# Clean up

echo
echo "Cleaning up..."

MAX_DAYS=7

/bin/rm -rf $HOME/core*
/usr/bin/find ~/ -maxdepth 1 -name gram_job\* -type f -mtime +$MAX_DAYS -exec rm -f {} \;
/usr/bin/find ~/ -maxdepth  1 -name gram_scratch\* -type d -mtime +$MAX_DAYS -exec rm -rf {} \;
/usr/bin/find ~/.globus/.gass_cache -mindepth 2 -type f -mtime +$MAX_DAYS -exec rm -f {} \;
/usr/bin/find ~/.globus/.gass_cache -depth -mindepth 2 -type d -empty -exec rm -rf {} \;
/usr/bin/find ~/.globus/job -type f -mtime +$MAX_DAYS -exec rm -f {} \;
/usr/bin/find ~/.globus/job -depth -type d -empty -exec rm -rf {} \;


if [ "x$GLIDEINS_WORK_DIR" != "x" ]; then
	touch $GLIDEINS_WORK_DIR
	/usr/bin/find $GLIDEINS_WORK_DIR/ -maxdepth 1 -type d -mtime +$MAX_DAYS -exec rm -rf {} \;
fi

#######################################################################
#
# End

echo
echo "Removing $WORK_DIR"
cd $START_DIR
rm -rf $WORK_DIR

echo
echo "=== SUCCESSFUL ==="
echo

