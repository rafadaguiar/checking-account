package com.rfna.checking_account.service.encoders_and_decoders

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import cats.syntax.either._
import com.rfna.checking_account.models.{Debt, Operation, OperationType}
import io.circe._
import io.circe.literal._
import io.circe.optics.JsonPath._
import org.bson.types.ObjectId

object Body {
  val dateParser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy")
  implicit val encodeDate: Encoder[Date] = Encoder.encodeString.contramap[Date](_.toString)
  implicit val decodeDate: Decoder[Date] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(dateParser.parse(str)).leftMap(t => "unable to parse Date")
  }
  implicit val encodeLocalDate: Encoder[LocalDate] = Encoder.encodeString.contramap[LocalDate](_.toString)
  implicit val decodeLocalDate: Decoder[LocalDate] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(LocalDate.parse(str)).leftMap(t => "unable to parse LocalDate")
  }
  implicit val encodeOperation: Encoder[Operation] = Encoder.instance[Operation] { op =>
    json"""{ "description": ${op.operationType.toString.toLowerCase()}, "amount": ${op.amount}, "date": ${op.date} }"""
  }
  implicit val decodeOperation: Decoder[Operation] = Decoder.instance[Operation] { c =>
    val json = c.focus.getOrElse(Json.Null)
    val description = OperationType.withName(root.description.string.getOption(json).get)
    val amount = root.amount.double.getOption(json).get
    val date = LocalDate.parse(root.date.string.getOption(json).get)

    Either.catchNonFatal(Operation(new ObjectId().toString, description, amount, date))
      .leftMap(t => DecodingFailure.fromThrowable(t, c.history))
  }
  implicit val encodeDebt: Encoder[Debt] = Encoder.instance[Debt] { debt =>
    if (debt.end.isDefined)
      json"""{ "start": ${debt.start}, "end": ${debt.end}, "amount": ${debt.amount} }"""
    else
      json"""{ "start": ${debt.start}, "amount": ${debt.amount} }"""
  }
}