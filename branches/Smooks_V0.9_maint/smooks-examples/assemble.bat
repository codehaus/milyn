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

pushd javabean-populator
call mvn assembly:assembly
popd

pushd model-driven-basic
call mvn assembly:assembly
popd

pushd profiling
call mvn assembly:assembly
popd

pushd xslt-basic
call mvn assembly:assembly
popd

pushd xslt-complex
call mvn assembly:assembly
popd

pushd xslt-groovy
call mvn assembly:assembly
popd

pushd xslt-namespaces
call mvn assembly:assembly
popd