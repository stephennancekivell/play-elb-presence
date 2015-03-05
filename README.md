Play ELB Presence
--------------

A Play Framework plugin to add and remove a ec2 instance from a Elastic Load Balancer.


Installation
------------

Add to your project's Build.scala
    
    libraryDependencies ++= Seq(
        "com.stephenn" %% "play-elb-presence" % "0.1"
    )


Add to your play.plugins

    100:com.stephenn.PlayElbPresence

Add to your application.conf

    elb.presence.elbName="elbName"
    elb.presence.accessKey="accessKey"
    elb.presence.secretKey="secretKey"
    elb.presence.region="aws-region"