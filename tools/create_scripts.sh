PWD=`dirname $0`
groovy -cp $PWD/target/tools-0.1.0-SNAPSHOT.jar  $PWD/src/main/groovy/bfx/support/CreateScriptLinks.groovy $*

