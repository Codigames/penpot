;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns app.main.ui.workspace.sidebar.options.shapes.image
  (:require
   [app.common.data.macros :as dm]
   [app.common.types.shape.layout :as ctl]
   [app.main.refs :as refs]
   [app.main.ui.workspace.sidebar.options.menus.blur :refer [blur-menu*]]
   [app.main.ui.workspace.sidebar.options.menus.constraints :refer [constraint-attrs constraints-menu*]]
   [app.main.ui.workspace.sidebar.options.menus.exports :refer [exports-menu* exports-attrs]]
   [app.main.ui.workspace.sidebar.options.menus.layer :refer [layer-attrs layer-menu*]]
   [app.main.ui.workspace.sidebar.options.menus.layout-container :refer [layout-container-flex-attrs layout-container-menu]]
   [app.main.ui.workspace.sidebar.options.menus.layout-item :refer [layout-item-attrs layout-item-menu]]
   [app.main.ui.workspace.sidebar.options.menus.measures :refer [measure-attrs measures-menu*]]
   [app.main.ui.workspace.sidebar.options.menus.shadow :refer [shadow-menu*]]
   [app.main.ui.workspace.sidebar.options.menus.slice-9 :refer [slice-9-menu*]]
   [rumext.v2 :as mf]))

(mf/defc options*
  [{:keys [shape file-id page-id]}]
  (let [id     (dm/get-prop shape :id)
        type   (dm/get-prop shape :type)
        ids    (mf/with-memo [id] [id])
        shapes (mf/with-memo [shape] [shape])

        applied-tokens
        (get shape :applied-tokens)

        measure-values
        (select-keys shape measure-attrs)

        layer-values
        (select-keys shape layer-attrs)

        constraint-values
        (select-keys shape constraint-attrs)

        layout-item-values
        (select-keys shape layout-item-attrs)

        layout-container-values
        (select-keys shape layout-container-flex-attrs)

        slice-9-values
        (select-keys shape [:slice-9])

        is-layout-child-ref
        (mf/with-memo [ids]
          (refs/is-layout-child? ids))

        is-layout-child?
        (mf/deref is-layout-child-ref)

        is-flex-parent-ref
        (mf/with-memo [ids]
          (refs/flex-layout-child? ids))

        is-flex-parent?
        (mf/deref is-flex-parent-ref)

        is-grid-parent-ref
        (mf/with-memo [ids]
          (refs/grid-layout-child? ids))

        is-grid-parent?
        (mf/deref is-grid-parent-ref)

        is-layout-child-absolute?
        (ctl/item-absolute? shape)]

    [:*
     [:> layer-menu* {:ids ids
                      :type type
                      :applied-tokens applied-tokens
                      :values layer-values}]
     [:> measures-menu* {:ids ids
                         :type type
                         :values measure-values
                         :applied-tokens applied-tokens
                         :shapes shapes}]

     [:> slice-9-menu* {:ids ids
                        :values slice-9-values}]

     [:& layout-container-menu
      {:type type
       :ids ids
       :values layout-container-values
       :applied-tokens applied-tokens
       :multiple false}]

     (when ^boolean is-layout-child?
       [:& layout-item-menu
        {:ids ids
         :type type
         :values layout-item-values
         :is-layout-child? true
         :is-flex-parent? is-flex-parent?
         :applied-tokens applied-tokens
         :is-grid-parent? is-grid-parent?
         :shape shape}])

     (when (or (not ^boolean is-layout-child?) ^boolean is-layout-child-absolute?)
       [:> constraints-menu* {:ids ids
                              :values constraint-values}])

     [:> shadow-menu* {:ids ids :values (get shape :shadow)}]

     [:> blur-menu* {:ids ids
                     :values (select-keys shape [:blur])}]

     [:> exports-menu* {:type type
                        :ids ids
                        :shapes shapes
                        :values (select-keys shape exports-attrs)
                        :page-id page-id
                        :file-id file-id}]]))
