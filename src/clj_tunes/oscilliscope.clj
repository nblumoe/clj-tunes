(ns clj-tunes.oscilliscope
  (:require [quil.core :as q]
            [quil.applet]))

;; for whatever reason, we need to create a first sketch before
;; starting overtone, otherwise an exception is thrown from time to
;; time
;; TODO dig into this and fix it
(q/defsketch foo :size [10 10])
(quil.applet/applet-close foo)

(use 'overtone.live)

(def sampled-wave (atom [[0 0]]))

(defn- clear-osc! [] (reset! sampled-wave [[0 0]]))

(defn- store-osc-sample! [value dt]
  (swap! sampled-wave #(conj % [(+ dt (first (last %)))
                                value])))

(defmacro sample-osc [trigger osc]
  `(demo 5 (send-reply ~trigger "/sample-osc" [~osc])))

(defmacro play-osc [osc]
  `(demo 1 ~osc))

(defn init-osc []
  (let [sample-freq 300
        sample-dt (/ 1 sample-freq)
        trigger (impulse sample-freq)]
    (on-event "/sample-osc" (fn [{[_ _ osc-value] :args}]
                              (store-osc-sample! osc-value sample-dt))
              ::sample-osc-listener)
    (clear-osc!)
    trigger))

(defn create! [& {:keys [width height max-time max-amplitude]
                  :or {width 600
                       height 300
                       max-time 5
                       max-amplitude 2}}]
  (let [time-scale (/ width max-time)
        amp-scale (/ height 2 max-amplitude)]

    (defn- setup []
      (q/smooth)
      (q/frame-rate 30)
      (q/background 0)
      (q/stroke 0 255 0)
      (q/fill 0 255 0))

    (defn- draw []
      (q/background 0)
      (q/with-translation [0 (/ (q/height) 2)]
        (loop [points @sampled-wave]
          (when (next points)
            (let [[x1 y1] (first points)
                  [x2 y2] (second points)]
              (q/line (* time-scale x1) (* amp-scale y1)
                      (* time-scale x2) (* amp-scale y2))
              (recur (next points)))))))

    (q/defsketch sine-plot
      :size [width height]
      :draw draw
      :setup setup
      :display 1)))

(defn osc-demo
  "Plot a oscillation and play a corresponding sound."
  []
  (create!)
  (clear-osc!)

  (let [trigger (init-osc)]
    (sample-osc trigger (+ (lf-tri:kr 3)
                           (lf-tri:kr (+ 1 (* 10 (sin-osc:kr 20)))))))

  (play-osc (+ (lf-tri 280)
               (lf-tri:ar (+ 100 (* 10 (sin-osc 8)))))))
