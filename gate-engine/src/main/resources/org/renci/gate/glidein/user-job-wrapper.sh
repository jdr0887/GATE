#!/bin/bash               

# change this to the maximum allowed data segment size (in kilobytes)
#ulimit -d 2000000

# load science portal environment                                    
. ~/.science-portal.rc                                               

# run the job                                                        
exec "$@"                                                           
