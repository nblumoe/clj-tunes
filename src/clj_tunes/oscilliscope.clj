(ns clj-tunes.oscilliscope
  (:require [quil.core :as q]
            [clj-tunes.tap :as tap]))

;; for whatever reason, we need to create a first sketch before
;; starting overtone, otherwise an exception is thrown from time to
;; time
;; TODO dig into this and fix it
(q/defsketch foo :size [10 10])
(quil.applet/applet-close foo)

(use 'overtone.live)

(defn- setup []
  (q/smooth)
  (q/frame-rate 30)
  (q/background 0)
  (q/stroke 0 255 0)
  (q/fill 0 255 0))

(defn create! [& {:keys [width height max-time max-amplitude]
                  :or {width 600
                       height 300
                       max-time 0.1
                       max-amplitude 2}}]
  (let [time-scale (/ width max-time)
        amp-scale (/ height 2 max-amplitude)]

    (defn- draw []
      (q/background 0)
      (q/with-translation [0 (/ (q/height) 2)]
        (loop [points (tap/get-buffered-taps)]
          (when (next points)
            (let [[x1 y1] (first points)
                  [x2 y2] (second points)]
              (q/line (* time-scale x1) (* amp-scale y1)
                      (* time-scale x2) (* amp-scale y2))
              (recur (next points)))))))

    (q/defsketch osc-plot
      :size [width height]
      :draw draw
      :setup setup
      :display 0)))

(defn demo-osc
  []
  (stop)
  (create!)
  (tap/clear-buffer!)
  (demo 1 (sin-osc 300))
  (tap/start-tapping))

(comment (demo-osc))
