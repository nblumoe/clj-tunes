(ns clj-tunes.oscilliscope
  (:require [quil.core :as q]
            [clj-tunes.tap :as tap]
            [overtone.core :refer :all]))

(defn- setup []
  (q/smooth)
  (q/frame-rate 30)
  (q/background 0)
  (q/stroke 0 255 0)
  (q/fill 0 255 0))

(defn- oscilloscope
  [& {:keys [max-time max-amplitude]
      :or {max-time 1
           max-amplitude 2}}]
  (let [time-scale (/ (q/width) max-time)
        amp-scale (/ (q/height) 2 max-amplitude)]
    (q/background 0)
    (q/with-translation [0 (/ (q/height) 2)]
      (loop [points (tap/get-buffered-taps)]
        (when (next points)
          (let [[x1 y1] (first points)
                [x2 y2] (second points)]
            (q/line (* time-scale x1) (* amp-scale y1)
                    (* time-scale x2) (* amp-scale y2))
            (recur (next points))))))))

;; TODO allow plotting multiple oscillators
(defn plot-osc [duration]
  (q/defsketch osc-plot
    :size [600 600]
    :draw (fn [] (oscilloscope :max-time duration))
    :setup setup
    :display 1)
  (stop)
  (tap/clear-buffer!))

(defn tap-sound [duration sample-path]
  (plot-osc duration)
  (sample sample-path)
  (tap/start-tapping :buffered? true :freq 300))

