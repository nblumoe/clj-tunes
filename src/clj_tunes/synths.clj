(ns clj-tunes.synths
  "A collection of synthesizers and instruments."
  (:require [overtone.core :refer :all]))

(defsynth fuzzit
  "A fuzzing sound."
  [freq 200 duration 1]
  (let [src (- (sin-osc freq (* 2 Math/PI (lf-saw 2)))
               (lf-saw freq))
        env (env-gen (perc 0.1 duration) :action FREE)]
    (out 0 (pan2 (* src env)))))

(definst gameboy
  "A gameboy like chiptune sound."
  [freq 300]
  (let [osc1 (square freq)
        osc2 (square (* 6/5 freq))
        osc3 (* 0.2 (square (* 2 freq)))
        osc-sum (+ osc1 osc2 osc3)
        osc-ring (sin (/ 6/5 freq))
        ringmod (ring1 osc-sum osc-ring)
        highpass (rhpf ringmod 300 0.03)
        env (env-gen (env-lin 0.01 0.4 0.1) FREE)
        output (* env highpass)]
    output))
