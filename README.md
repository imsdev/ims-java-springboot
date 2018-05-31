# Spring Boot samples for accessing IMS TM and IMS DB 

The examples in this repository demonstrate how you can access IMS transactions or databases in an unmanaged environment such as Spring Boot. 

* [IMS synchronous callout processing with Spring Boot](https://github.com/imsdev/ims-java-springboot/tree/master/ims-springboot-callout) <br/>
This example demonstrates how to call IMS transactions as microservices by using the IMS TM resource adapter, 
with connection pooling, in an un-managed environment. 
* [Accessing IMS transactions using IMS TM Resource Adapter with connection pooling](https://github.com/imsdev/ims-java-springboot/tree/master/ims-springboot-tm) <br/> 
This example demonstrates how to process a synchronous callout from IMS by using the IMS TM resource adapter and a message-driven bean in Spring Boot.
* [Accessing IMS data using IMS Universal drivers](https://github.com/imsdev/ims-java-springboot/tree/master/ims-springboot-jdbc) <br/>
This example demonstrates how to issue SQL queries against an IMS database using the IMS Universal drivers in an unmanaged environment such as Spring Boot.

## Featured technologies

* [Spring Framework](https://spring.io/): An open source application framework and inversion of control container for the Java platform.
* [Spring Boot](http://projects.spring.io/spring-boot/)</a>: Spring Boot is a framework developed on top of the Spring framework to ease the development of new Spring applications. 
* [IMS TM Resource Adapter](https://www.ibm.com/support/knowledgecenter/SSEPH2_14.1.0/com.ibm.ims14.doc.tmra/topics/tmresoverview.htm): 
A Java EE connector architecture (JCA)-compliant resource adapter that allows Java applications or services to access IMS transactions via IMS Connect. It also enables IMS applications to call out to external Java applications or services.
* [IMS Universal drivers](https://www.ibm.com/support/knowledgecenter/SSEPH2_14.1.0/com.ibm.ims14.doc.apg/ims_odbjdbcintro.htm): 
A set of drivers or resource adapters that provide Java applications with connectivity and access to IMS databases from z/OS® and distributed (non-z/OS) environments through TCP/IP.
