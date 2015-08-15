(ns clj-tunes.synths
  (:require [overtone.core :refer :all]))

(defsynth fuzzit [freq 200
                  duration 1]
  (let [src (- (sin-osc freq (* 2 Math/PI (lf-saw 2)))
               (lf-saw freq))
        env (env-gen (perc 0.1 duration) :action FREE)]
    (out 0 (pan2 (* src env)))))
