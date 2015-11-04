(ns clj-tunes.oscilloscope
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
  (let [time-scale (/ (* 3 (q/width)) max-time)
        amp-scale (/ (* 2 (q/height)) 2 max-amplitude)]
    (q/background 0)
    (q/with-translation [0 (/ (q/height) 2)]
      (loop [points (tap/get-buffered-taps)]
        (when (next points)
          (let [[x1 y1] (first points)
                [x2 y2] (second points)]
            (q/line (* time-scale x1) (* amp-scale y1)
                    (* time-scale x2) (* amp-scale y2)) ;try flipping the y value (negating it)
                                                        ; try switching x and y 
            (recur (next points))))))))

;; TODO allow plotting multiple oscillators
(defn plot-osc [duration]
  (q/defsketch osc-plot
    :size [600 600]
    :draw (fn [] (oscilloscope :max-time duration))
    :setup setup
    :display 0)
  (stop)
  (tap/clear-buffer!))

;(def sound-path (sample "/home/tamara/Desktop/activity_unproductive.wav"))
;(defn tap-sound [duration sound]
;  (plot-osc duration)
;  (sound)
;  (tap/start-tapping :buffered? true :freq 300))

(defn tap-sound [duration sound-path]
  (plot-osc duration)
  (let [sound-map (sample sound-path)]
    (sound-map))
  (tap/start-tapping :buffered? true :freq 20))

(def sample-buf (load-sample "/home/tamara/Desktop/activity_unproductive.wav"))

(defsynth reverb-on-left []
  (let [dry (play-buf 1 sample-buf)
    wet (free-verb dry 1)]
    (out 0 [wet dry])))


