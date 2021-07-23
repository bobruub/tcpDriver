@echo off

set PROGNAME=vie_test
set HOMELOC=C:\Users\Bob\Dropbox\java\20140618
set JAVALOC="C:\Program Files (x86)\Java\jre7\bin"

cd /d %HOMELOC%\command

set CLASSPATH=%HOMELOC%\vie.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\lava3-printf.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\lava3-core.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.axis2.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.commonservices.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.defaultconfig.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.headers.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.jmqi.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.jms.Nojndi.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.pcf.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.postcard.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.soap.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mq.tools.ras.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mqbind.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\com.ibm.mqjms.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\commons-lang3-3.2.1.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\connector.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\jai_imageio-1.1.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\jai_imageio_windows-i586.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\java-rt-jar-stubs-1.5.0.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\jcommon-1.0.17.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\jfreechart-1.0.14.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\lava3-core.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\lava3-printf.jar
set CLASSPATH=%CLASSPATH%;%HOMELOC%\lib\log4j-1.2.17.jar

rem echo %CLASSPATH%
echo %JAVALOC%
echo %HOMELOC%\xml\vie_test.xml


%JAVALOC%\java -Xmx512m com.Stub.http.httpsStub %HOMELOC%\xml\vie_test.xml

