(ns clj-tunes.core
  (:require [quil.core :as q]
            [clj-tunes.synths :as synths]
            [clj-tunes.oscilliscope :as osc]))

;; for whatever reason, we need to create a first sketch before
;; starting overtone, otherwise an exception is thrown from time to
;; time
;; TODO dig into this and fix it
(q/defsketch foo :size [10 10])
(quil.applet/applet-close foo)

(use 'overtone.live)

(osc/plot-osc 1 synths/fuzzit 300 1)

(stop)
