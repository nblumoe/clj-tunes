(ns clj-tunes.tap
  (:require [overtone.core :refer :all]))

(def ^:private ^:dynamic *taps* nil)

(def ^:private tap-buffer (atom [[0 0]]))

(defn- store-to-buffer!
  "Updates the tap buffer atom by appending the given value. The
  time variable for the appended data point is the timestamp of the
  last point in the existing vectore plus dt."
  [value dt]
  (swap! tap-buffer #(conj % [(+ dt (first (last %)))
                                value])))

(defn- register-tap-buffer
  [active? dt]
  (if active?
    (on-event
     "/overtone/tap"
     (fn [{[_ _ value] :args}]
       (store-to-buffer! value dt))
     ::tapper-listener)
    (remove-event-handler ::tapper-listener)))

(defsynth tapper
  "A synth to tap the stereo output bus."
  [bus 0 freq 500]
  (tap :stereo-out freq (in:ar bus)))

(defn start-tapping
  [& {:keys [buffered?
             freq]
      :or {freq 100}}]
  (let [tapper (tapper :freq freq)
        taps (:taps tapper)
        dt (/ 1 freq)]
    (println "---" buffered?)
    (register-tap-buffer buffered? dt)
    ;; current value of the output bus can be retrieved via get-taps
    (alter-var-root (var *taps*) (constantly taps))))

(defn get-taps
  "Deref and return all of our taps as a plain old map."
  []
  (when *taps* (into {} (map (fn [[k v]] [k @v]) *taps*))))

(defn get-buffered-taps [] @tap-buffer)

(defn clear-buffer!
  "Resets the buffered tap data."
  []
  (reset! tap-buffer [[0 0]]))
