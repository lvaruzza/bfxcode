mkdir -p bfxtools
cp -Rv target/lib bfxtools
cp -Rv target/tools*.jar bfxtools
cp bfx.dist bfxtools/bfx
tar cjvf bfxtools.tar.bz2 bfxtools
