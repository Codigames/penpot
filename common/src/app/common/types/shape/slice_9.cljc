;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns app.common.types.shape.slice-9)

;; 9-slice margins for image shapes, in source-image pixels. The four
;; margins (top, right, bottom, left) split the image into 9 regions:
;; corners keep their size, edges stretch along one axis, and the
;; center stretches along both. Stretch only (no tiling).

(def default
  {:enabled? false
   :top 0
   :right 0
   :bottom 0
   :left 0})

(defn set-slice-9
  [shape s9]
  (if (nil? s9)
    (dissoc shape :slice-9)
    (assoc shape :slice-9 s9)))

(defn set-margin
  [shape attr value]
  (update shape :slice-9 (fn [s9]
                           (assoc (or s9 default) attr value))))

(defn set-enabled
  [shape enabled?]
  (update shape :slice-9 (fn [s9]
                           (assoc (or s9 default) :enabled? (boolean enabled?)))))

(defn toggle-enabled
  [shape]
  (set-enabled shape (not (get-in shape [:slice-9 :enabled?]))))
