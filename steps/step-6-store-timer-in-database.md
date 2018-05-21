# 1. Add a tiny database framework

The framework can be reused in other app.

# 2. Store timers in database

* Add a class 'TimerTable', to deal with table.
* Change code in MainActivity to.

# Reference

In 'TimerTable.java', column 'id' is defined as 'integer primary key'.

But in 'TimerItem.java', field 'id' is defined as 'long'.

That's because 'integer' in SQLite *is a signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes depending on the magnitude of the value.*

See:
* [Datatypes In SQLite Version 3](http://www.sqlite.org/datatype3.html)
* [SQLite Autoincrement](http://www.sqlite.org/autoinc.html)

