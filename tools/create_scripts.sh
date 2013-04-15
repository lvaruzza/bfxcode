PWD=`dirname $0`
groovy -cp $PWD/target/tools-0.1.0-SNAPSHOT.jar  $PWD/scripts/CreateScriptLinks.groovy $*

