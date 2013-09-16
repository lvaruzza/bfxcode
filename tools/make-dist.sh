version=`cat VERSION`
mkdir dist
cd dist
rm -Rf bfxtools-*
mkdir -p bfxtools-$version
mkdir bfxtools-$version/lib
cp -Rv ../target/lib/* bfxtools-$version/lib
cp -Rv ../target/tools*.jar bfxtools-$version
sed "s/@VERSION@/$version/g" ../bfx.dist > bfxtools-$version/bfx
chmod +x bfxtools-$version/bfx
tar cjvf bfxtools-$version.tar.bz2 bfxtools-$version
rm -Rf bfxtools-$version
