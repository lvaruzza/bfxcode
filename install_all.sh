base=`dirname $0`
cd $base

for dir in bfx-core bfx-tools; do
    echo $dir
    cd $dir
    mvn package install
    cd ..
done
 
