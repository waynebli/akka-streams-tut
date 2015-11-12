package com.fourgee.streams.http.server

import java.nio.charset.Charset

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.fourgee.streams.database.Database

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.apply
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONDocument

object Boot extends App {

  // the actor system to use. Required for flowmaterializer and HTTP.
  // passed in implicit
  implicit val system = ActorSystem("Streams")
  implicit val materializer = ActorMaterializer()

  // start the server on the specified interface and port.
  val serverBinding2 = Http().bindAndHandleAsync({
    case req @ HttpRequest(_, Uri.Path("/getAllTickers"), _, _, _) => {
      // make a db call, which returns a future.
      // use for comprehension to flatmap this into
      // a Future[HttpResponse]
      for {
        input <- Database.findAllTickers
      } yield {
        HttpResponse(entity = convertToString(input))
      }
    }

    // match GET pat. Return a single ticker
    case req @ HttpRequest(_, Uri.Path("/get"), _, _, _) => {

      // next we match on the request parameter
      req.uri.query(Charset.defaultCharset(), Uri.ParsingMode.Relaxed).get("ticker") match {

        // if we find the query parameter
        case Some(queryParameter: String) => {

          // query the database
          val ticker = Database.findTicker(queryParameter)

          // use a simple for comprehension, to make
          // working with futures easier
          for {
            t <- ticker
          } yield {
            t match {
              case Some(bson) => HttpResponse(entity = convertToString(bson))
              case None => HttpResponse(status = StatusCodes.OK)
            }
          }
        }

        // if the query parameter isn't there
        case None => Future(HttpResponse(status = StatusCodes.OK))
      }
    }

    // Simple case that matches everything, just return a not found
    case HttpRequest(_, _, _, _, _) => {
      Future[HttpResponse] {
        HttpResponse(status = StatusCodes.NotFound)
      }
    }

  }, interface = "localhost", port = 8091)

  def convertToString(input: List[BSONDocument]): String = {
    input.map(f => convertToString(f))
      .mkString("[", ",", "]")
  }

  def convertToString(input: BSONDocument): String = {
    Json.stringify(BSONFormats.toJSON(input))
  }

}