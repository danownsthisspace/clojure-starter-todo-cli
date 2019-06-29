(ns todo.core
  (:require [clojure.string :as str]
            ; Packages need to nbe added to the project.clj file before
            ; they can be added here.
            ; Import the term color package which colours text in the terminal.
            ; reference it as clr
            [clojure.term.colors :as clr])
  (:gen-class))

; Use this atom to store a list of tasks
(def task-store (atom []))

(defn store-task!
  "Adds a task to the task store"
  [task-name]
  (swap! task-store conj {:name task-name :id (str (count @task-store))})
  (println "Task saved!\n"))

(defn print-tasks
  "Prints all the tasks and their indexes"
  []
  (println (clr/underline "Here is a list of your current tasks:"))
  (doseq [task @task-store]
    (let [{:keys [name id]} task]
      (println (str "[" id "]") (clr/reverse-color name))))
  (println (apply str (repeat 40 "=")) "\n"))

(defn remove-task!
  "Removes a task from the task-store"
  [id]
  (if-let [task-to-delete (first (filter #(= (:id %) id) @task-store))]
    (do
      (reset! task-store (remove #(= (:id %) id) @task-store))
      (println "Deleted: " (:name task-to-delete)))
    (println (clr/red (str "Task with id [" id "] not found!")))))

; Define a map where the key is the command
; and the value is a map with the function and
; the description of the command.
(def commands
  {:add    {:description "adds a task to your task list"
            :fn          #(store-task! %)}
   :list   {:description "Lists all tasks"
            :fn          (fn [_] (print-tasks))}
   :delete {:description "Deletes a task from your list based on its index"
            :fn          #(remove-task! %)}})

(defn print-commands
  "Prints out all available commands"
  [commands]
  (doseq [command (keys commands)]
    (let [shortcut (name command)
          descr (get-in commands [command :description] "")]
      (println (clr/yellow (str "* " shortcut ": " descr)))))
  (println (apply str (repeat 40 "=")) "\n"))

(defn -main
  "Run the application"
  [& args]
  (println "Available commands:")
  (print-commands commands)
  (loop []
    (let [input (str/split (read-line) #" ")
          command-name (-> input
                           (first)
                           (str/lower-case)
                           (keyword))
          command-fn (get-in commands [command-name :fn])
          args (str/join " " (rest input))]
      (if (= command-name :quit)
        (println "bye")
        ; else
        (do
          (when command-fn (command-fn args))
          (recur))))))