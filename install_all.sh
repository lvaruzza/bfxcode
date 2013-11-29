base=`dirname $0`
cd $base
./core/lib/install_libs.sh

for dir in compilation-toolbox core tools; do
    echo $dir
    cd $dir
    mvn package install
    cd ..
done
 
