;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns app.main.ui.workspace.sidebar.options.menus.slice-9
  (:require-macros [app.main.style :as stl])
  (:require
   [app.common.types.shape.slice-9 :as cts9]
   [app.main.data.workspace.shapes :as dwsh]
   [app.main.store :as st]
   [app.main.ui.components.numeric-input :refer [numeric-input*]]
   [app.main.ui.components.title-bar :refer [title-bar*]]
   [app.main.ui.ds.buttons.icon-button :refer [icon-button*]]
   [app.main.ui.ds.foundations.assets.icon :as i]
   [app.util.i18n :refer [tr]]
   [rumext.v2 :as mf]))

(mf/defc slice-9-menu*
  [{:keys [ids values]}]
  (let [slice-9      (:slice-9 values)
        has-value?   (some? slice-9)
        hidden?      (and has-value? (not (:enabled? slice-9)))

        state*       (mf/use-state true)
        open?        (deref state*)
        toggle-open  (mf/use-fn #(swap! state* not))

        change!
        (mf/use-fn
         (mf/deps ids)
         (fn [update-fn]
           (st/emit!
            (dwsh/update-shapes ids update-fn
                                {:reg-objects? true
                                 :attrs [:slice-9]}))))

        handle-add
        (mf/use-fn
         (mf/deps change!)
         (fn []
           (change! #(cts9/set-slice-9 % (assoc cts9/default :enabled? true)))))

        handle-delete
        (mf/use-fn
         (mf/deps change!)
         (fn []
           (change! #(cts9/set-slice-9 % nil))))

        handle-toggle-visibility
        (mf/use-fn
         (mf/deps change!)
         (fn []
           (change! cts9/toggle-enabled)))

        handle-change
        (mf/use-fn
         (mf/deps change!)
         (fn [attr value]
           (change! #(cts9/set-margin % attr (or value 0)))))

        on-top-change    #(handle-change :top %)
        on-right-change  #(handle-change :right %)
        on-bottom-change #(handle-change :bottom %)
        on-left-change   #(handle-change :left %)]

    [:div {:class (stl/css :element-set)}
     [:div {:class (stl/css :element-title)}
      [:> title-bar* {:collapsable  has-value?
                      :collapsed    (not open?)
                      :on-collapsed toggle-open
                      :title        (tr "workspace.options.slice-9.title")}
       (if has-value?
         [:div {:class (stl/css :actions)}
          [:> icon-button* {:variant "ghost"
                            :aria-label (tr "workspace.options.slice-9.toggle")
                            :on-click handle-toggle-visibility
                            :icon (if hidden? i/hide i/shown)}]
          [:> icon-button* {:variant "ghost"
                            :aria-label (tr "workspace.options.slice-9.remove")
                            :on-click handle-delete
                            :icon i/remove}]]
         [:> icon-button* {:variant "ghost"
                           :aria-label (tr "workspace.options.slice-9.add")
                           :on-click handle-add
                           :icon i/add}])]]

     (when (and open? has-value?)
       [:div {:class (stl/css :element-set-content)}
        [:div {:class (stl/css :slice-9-grid)}
         [:div {:class (stl/css :small-input)}
          [:> numeric-input*
           {:placeholder "--"
            :title (tr "workspace.options.slice-9.top")
            :min 0
            :on-change on-top-change
            :value (:top slice-9)}]]
         [:div {:class (stl/css :small-input)}
          [:> numeric-input*
           {:placeholder "--"
            :title (tr "workspace.options.slice-9.right")
            :min 0
            :on-change on-right-change
            :value (:right slice-9)}]]
         [:div {:class (stl/css :small-input)}
          [:> numeric-input*
           {:placeholder "--"
            :title (tr "workspace.options.slice-9.bottom")
            :min 0
            :on-change on-bottom-change
            :value (:bottom slice-9)}]]
         [:div {:class (stl/css :small-input)}
          [:> numeric-input*
           {:placeholder "--"
            :title (tr "workspace.options.slice-9.left")
            :min 0
            :on-change on-left-change
            :value (:left slice-9)}]]]])]))
