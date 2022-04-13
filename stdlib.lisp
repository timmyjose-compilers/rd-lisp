;;; the standard library for rd-lisp

(defun abs (n)
  (if (< n 0)
    (- 0 n)
    n))

(defun foldl (fn init lst)
  (if lst
    (foldl fn 
           (fn init (car lst))
           (cdr lst))
    init))

(defun foldr (fn init lst)
  (if lst
    (fn (car lst)
        (foldr fn init (cdr lst)))
    init))

(defun reverse (lst)
  (foldl (lambda (lst elem) (cons elem lst)) nil lst))

(defun map (fn lst)
  (foldr (lambda (x rest)
           (cons (fn x) rest))
         nil
         lst))

(defun filter (pred lst)
  (foldr (lambda (x rest)
           (if (pred x)
             (cons x rest)
             rest))
         nil
         lst))

(defun list (&rest items)
  items)

(defun list* (&rest forms)
  (foldr 
    (lambda (e acc)
      (if (pair? e)
        (if (null? acc)
          (append e acc)
          (cons e acc))
        (cons e acc)))
    nil
    forms))

(defun caar (x) (car (car x)))
(defun cadr (x) (car (cdr x)))
(defun cddr (x) (cdr (cdr x)))

(defun append-two (lst1 lst2)
  (if (null? lst1)
    lst2
    (cons (car lst1) (append-two (cdr lst1) lst2))))

(defun append (&rest lsts)
  (foldr 
    (lambda (lst acc)
      (append-two lst acc))
    nil
    lsts))
