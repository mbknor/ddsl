Things to do when releaseing or uping versions.
-----------------------------------------------

 * first set final version for core.
 * change dependency version in example
 * change dependency version in ddsl-webcontainer-auto
 * change dependency version in ddsl-cmdline-tool
 * commit it
 * sbt publish ddsl-core
 * sbt publish ddsl-webcontainer-auto
 * sbt publish ddsl-cmdline-tool
 
 * update scaladoc to mbknor.github.com
 * commit changes to mbknor.github.com
 

 * git push ddsl
 * git push mbknor.github.com

