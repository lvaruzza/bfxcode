for i in `find -name pom.xml`; do
    dir=`dirname $i`
    cd $dir
    mvn install
    cd ..
done
 
