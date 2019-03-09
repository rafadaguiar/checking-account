FROM hseeberger/scala-sbt:8u181_2.12.8_1.2.8

ENV CHECKING_ACCOUNT_ENV=docker

RUN useradd -ms /bin/bash purplebanker
USER purplebanker
WORKDIR /home/purplebanker
ADD --chown=purplebanker . .

RUN sbt compile
CMD sbt run