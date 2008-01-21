pushd css
call mvn assembly:assembly
popd

pushd csv-to-xml
call mvn assembly:assembly
popd

pushd edi-to-java
call mvn assembly:assembly
popd

pushd edi-to-xml
call mvn assembly:assembly
popd

pushd java-basic
call mvn assembly:assembly
popd

pushd java-to-java
call mvn assembly:assembly
popd

pushd java-to-xml
call mvn assembly:assembly
popd

pushd model-driven-basic
call mvn assembly:assembly
popd

pushd model-driven-basic-virtual
call mvn assembly:assembly
popd

pushd mule-esb-integration
call mvn assembly:assembly
popd

pushd profiling
call mvn assembly:assembly
popd

pushd servlet\war
call mvn assembly:assembly
popd

pushd sj-testimonial
call mvn assembly:assembly
popd

pushd xml-to-java
call mvn assembly:assembly
popd

pushd xml-to-java-virtual
call mvn assembly:assembly
popd

pushd xslt-basic
call mvn assembly:assembly
popd

pushd xslt-groovy
call mvn assembly:assembly
popd