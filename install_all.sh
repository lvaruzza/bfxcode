base=`dirname $0`
cd $base

for dir in core tools; do
    echo $dir
    cd $dir
    mvn package install
    cd ..
done
 
