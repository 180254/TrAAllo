## TrAAllo
<br />
University: Lodz University of Technology<br />
Faculty: Faculty of Electrical, Electronic, Computer and Control Engineering<br />
Subject: Web application development<br />
Task: Trello clone/interpretation.<br />
Lang: Java, Framework: Play Framework<br />
<br />
Group: "Play" team<br />
<br />
#### How to run this?
0. Make sure you have installed  [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
1. Download [Typesafe Activator 1.3.6 for Play 2.4.3](https://downloads.typesafe.com/typesafe-activator/1.3.6/typesafe-activator-1.3.6-minimal.zip)
2. [Add activator your PATH](https://playframework.com/documentation/2.4.x/Installing) or unpack it to TrAAllo directory.
3. Run TrAAllo from command line by "activator run" command (NOT "activator ui") from TrAAllo directory
4. If it is first application start it may take a while - activator will download dependencies
5. Wait until that message is displayed:<br/>
--- (Running the application, auto-reloading is enabled) ---<br/>
[info] p.c.s.NettyServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000<br/>
(Server started, use Ctrl+D to stop and go back to the console...)<br/>
6. Open (http://127.0.0.1:9000/)[http://127.0.0.1:9000/] with your favorite browser.
7.  It may take a while - activator will compile sources 
8.  If application do not start (browser "loading" webpage a lot of time, and there is no more message in console), try open it again and/or click "enter" in console with activator
