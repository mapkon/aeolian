(ns aeolian.midi.drums)

(def hi-hat "42")
(def clap "39")
(def stick "37")
(def beat stick)

(def four-beat-metro (str "%%MIDI drum dddd " beat " " beat " " beat " " beat " ""\n"))
(def no-accents "%%MIDI nobeataccents")
(def first-beat-emphasis "%%MIDI beatstring pmmm\n")

(def drum-track (str "%%MIDI program 32\n" first-beat-emphasis four-beat-metro no-accents "%%MIDI drumon\n%%MIDI gchord c\n"))

(defn volume-for [method-length]
	"")
