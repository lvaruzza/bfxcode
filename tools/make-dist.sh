version=`cat VERSION`
#rm -Rf bfxtools-*
#mkdir -p bfxtools-$version
#cp -Rv target/lib bfxtools-$version
#cp -Rv target/tools*.jar bfxtools-$version
sed "s/@VERSION@/$version/g" bfx.dist > bfxtools-$version/bfx
chmod +x bfxtools-$version/bfx
#tar cjvf bfxtools-$version.tar.bz2 bfxtools-$version
