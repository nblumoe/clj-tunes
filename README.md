# clj-tunes

An Overtone demo project for Clojure Dojo learning groups in Berlin.
The learning groups originate in the [ClojureBridge][4] event in
Berlin, 2015.

# Installation

## Requirements

- a working [Leiningen][1] setup
- a working [Overtone][2] setup, which might require additional steps on
  Linux, please refer to the corresponding documentation about
  [Jack][6] and [SuperCollider][3]

## Test your installation

1. Start a repl (e.g. with `lein repl` or from your IDE).
2. Run `(use 'overtone.live)`
3. Run `(demo (sin-osc))`

If everything is working, you should hear a short, nice and clear
[sine sound][5].

## License

GNU Affero General Public License v3

![AGPLv3](https://gnu.org/graphics/agplv3-155x51.png)

[1]: http://leiningen.org/
[2]: https://github.com/overtone/overtone
[3]: https://github.com/overtone/overtone/wiki/Connecting-scsynth
[4]: http://www.clojurebridge.org/
[5]: https://en.wikipedia.org/wiki/Sine_wave#Occurrences
[6]: https://github.com/overtone/overtone/wiki/Installing-and-starting-jack
