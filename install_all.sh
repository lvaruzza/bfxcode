for i in `find -maxdepth 2 -name pom.xml`; do
    dir=`dirname $i`
    echo $dir
    cd $dir
    mvn package install
    cd ..
done
 
