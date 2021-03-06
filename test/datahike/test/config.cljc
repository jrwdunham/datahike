(ns datahike.test.config
  (:require
   #?(:cljs [cljs.test :as t :refer-macros [is are deftest testing]]
      :clj  [clojure.test :as t :refer [is are deftest testing use-fixtures]])
   [datahike.config :refer :all]))

(deftest int-from-env-test
  (is (= 1000
         (int-from-env :foo 1000))))

(deftest bool-from-env-test
  (is (bool-from-env :foo true)))

(deftest uri-test
  (let [mem-uri "datahike:mem://config-test"
        file-uri "datahike:file:///tmp/config-test"
        level-uri "datahike:level:///tmp/config-test"
        pg-uri "datahike:pg://alice:foo@localhost:5432/config-test"]

    (are [x y] (= x (uri->config y))
      {:backend :mem :host "config-test" :uri mem-uri}
      mem-uri

      {:backend :file :path "/tmp/config-test" :uri file-uri}
      file-uri

      {:backend :level :path "/tmp/config-test" :uri level-uri}
      level-uri

      {:backend :pg
       :host "localhost" :port 5432 :username "alice" :password "foo" :path "/config-test"
       :uri pg-uri}
      pg-uri)))

(deftest deprecated-test
  (let [mem-cfg {:backend :mem
                 :host "deprecated-test"}
        file-cfg {:backend :file
                  :path "/deprecated/test"}
        default-new-cfg {:keep-history? true
                         :initial-tx nil
                         :index :datahike.index/hitchhiker-tree
                         :schema-flexibility :write}]
    (is (= (merge default-new-cfg
                  {:store {:backend :mem :id "deprecated-test"}})
           (from-deprecated mem-cfg)))
    (is (= (merge default-new-cfg
                  {:store {:backend :file
                           :path "/deprecated/test"}})
           (from-deprecated file-cfg)))))

(deftest load-config-test
  (testing "configuration defaults"
    (let [{:keys [store name] :as config} (load-config)]
      (is (= {:store {:backend :mem
                      :id "default"}
              :keep-history? true
              :schema-flexibility :write
              :index :datahike.index/hitchhiker-tree}
             (-> config (dissoc :name)))))))

