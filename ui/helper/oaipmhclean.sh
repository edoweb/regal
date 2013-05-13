mysqldump -uproai -pproaipwd --add-drop-table --no-data proai|grep ^DROP|mysql -uproai -pproaipwd proai
