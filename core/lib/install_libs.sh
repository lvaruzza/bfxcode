dir=`dirname $0`
mvn install:install-file -Dfile=$dir/sam-1.87.jar -DgroupId=picard -DartifactId=sam -Dversion=1.87 -Dpackaging=jar -DgeneratePom=true
