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

(def ^:private
  ^:dynamic
  *taps* nil)

(def sampled-wave (atom [[0 0]]))

(defn- clear-osc! [] (reset! sampled-wave [[0 0]]))

(defn- store-osc-sample!
  "Updates the sampled-wave atom by appending the given value. The
  time variable for the appended data point is the timestamp of the
  last point in the existing vectore plus dt."
  [value dt]
  (swap! sampled-wave #(conj % [(+ dt (first (last %)))
                                value])))

(defsynth tapper
  "A synth to tap the stereo output bus."
  [bus 0 freq 300]
  (tap :stereo-out freq (in:ar bus)))

(defn start-tapping []
  (let [tapper (tapper)
        taps (:taps tapper)
        freq (get-in tapper [:args "freq"])
        dt (/ 1 freq)]
    ;; current value of the output bus can be retrieved via get-taps
    (alter-var-root (var *taps*) (constantly taps))
    ;; on every sample tap event, append time and amplitude to an atom
    (on-event "/overtone/tap" (fn [{[_ _ value] :args}]
                                (store-osc-sample! value dt))
              ::tapper-listener)))

(defn get-taps
  "Deref and return all of our taps as a plain old map."
  []
  (when *taps* (into {} (map (fn [[k v]] [k @v]) *taps*))))

(defn create! [& {:keys [width height max-time max-amplitude]
                  :or {width 600
                       height 300
                       max-time 3
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

    (q/defsketch osc-plot
      :size [width height]
      :draw draw
      :setup setup
      :display 1)))

(defn demo-osc
  []
  (stop)
  (clear-osc!)
  (create!)
  (demo 2 (sin-osc 3))
  (start-tapping))
