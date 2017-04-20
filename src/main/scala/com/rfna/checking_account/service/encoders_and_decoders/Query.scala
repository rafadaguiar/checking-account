package com.rfna.checking_account.service.encoders_and_decoders

import java.time.LocalDate

import cats.data._
import org.http4s.dsl.QueryParamDecoderMatcher
import org.http4s.{ParseFailure, QueryParamDecoder, QueryParameterValue}

object Query {
  implicit val localDateQueryParamDecoder = new QueryParamDecoder[LocalDate] {
    def decode(queryParamValue: QueryParameterValue): ValidatedNel[ParseFailure, LocalDate] = {
      QueryParamDecoder.decodeBy[LocalDate, String](LocalDate.parse).decode(queryParamValue)
    }
  }

  object StartDateMatcher extends QueryParamDecoderMatcher[LocalDate]("start_date")

  object EndDateMatcher extends QueryParamDecoderMatcher[LocalDate]("end_date")

}