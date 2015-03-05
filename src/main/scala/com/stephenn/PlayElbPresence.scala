package com.stephenn.playelbpresence

import scala.collection.JavaConverters._
import play.api.{Plugin, Application, Logger}
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions._
import com.amazonaws.services.elasticloadbalancing._
import com.amazonaws.services.elasticloadbalancing.model.{RegisterInstancesWithLoadBalancerRequest, DeregisterInstancesFromLoadBalancerRequest, Instance}

class PlayElbPresence(app: Application) extends Plugin {
  val logger: Logger = Logger(this.getClass())

  val getInstanceUrl = "http://169.254.169.254/latest/meta-data/instance-id"

  def withElb(f: (String) => Unit): Unit = {
    app.configuration.getString("elb.presence.elbName") match {
      case Some(elbName) => f(elbName)
      case None => logger.warn("no elbName configured")
    }
  }

  def withInstanceId(f: (String) => Unit): Unit = {
    WS.url(getInstanceUrl)(app).get().map { resp =>
      f(resp.body)
    }
  }

  def withClient[T](f: (AmazonElasticLoadBalancingClient) => Unit): Unit = {
    (app.configuration.getString("elb.presence.accessKey"),
      app.configuration.getString("elb.presence.secretKey"),
      app.configuration.getString("elb.presence.region")) match {
      case (Some(accessKey), Some(secretKey), Some(region)) => {
        val credentials = new BasicAWSCredentials(accessKey, secretKey)
        val c = new AmazonElasticLoadBalancingClient(credentials)
        c.setRegion(Region.getRegion(Regions.fromName(region)))
        f(c)
      }
      case _ => logger.warn("could not start client, not accessKey, secretKey or region")
    }
  }

  override def onStart() = {
    logger.info("plugin start")

    withClient { client =>
      withElb { elbName =>
        withInstanceId { instanceId =>
          val instances = List(new Instance(instanceId)).asJava

          client.registerInstancesWithLoadBalancer(new RegisterInstancesWithLoadBalancerRequest(elbName, instances))
        }
      }
    }
  }

  override def onStop() = {
    logger.info("plugin stop")

    withClient { client =>
      withElb { elbName =>
        withInstanceId { instanceId =>
          val instances = List(new Instance(instanceId)).asJava

          val res = client.deregisterInstancesFromLoadBalancer(new DeregisterInstancesFromLoadBalancerRequest(elbName, instances))
          logger.info("res "+res)
          logger.info("plugin stop end")
        }
      }
    }
  }
}