"# Calendar_ViewPager" 
Bài tập Viewpager: App Lịch <dùng viewpager>
- có thể chọn/setting được thứ bắt đầu của tuần
- trên màn hình sẽ hiển thị lịch của 1 tháng, có text hiển thị tháng hiện tại ở trên cùng, các ngày không phải của tháng đấy sẽ có màu text mờ đi
- các ngày trong tháng ngăn cách nhau bằng 1 line, khi chọn 1 ngày sẽ đổi màu ô đấy, khi double tab 1 ô sẽ đổi sang 1 màu khác
- có thể chuyển sang tháng tiếp theo hoặc trước đó bằng cách vuốt sang trái hoặc phải
- có thể thêm bất kỳ tính năng nào khác

------------------------------------------------
Database
Xây dựng 1 app ghi lại nhật ký hàng ngày có các chức năng sau:
- thiết lập mật khẩu cho app: bất cứ khi nào mở app đều phải show màn nhập pass(nếu đã có setting pass) -> dùng sharepreference
- viết nhật ký: ng dùng có thể chọn ngày viết nhật ký, người dùng có thể viết bất kỳ ký tự gì (lưu vào DB, dùng sqlite hoặc Room)
- màn hình calendar: ở màn hình này, những ngày nào đã có viết nhật ký thì đổi màu background, khi bouble click vào 1 ngày nào đấy thì mở đến màn nhập và fill datetime của ngày đã chọn
- 1 màn hình show tất cả nhật ký dưới dạng list sắp xếp theo ngày tháng, có chức năng tìm kiếm theo nội dung nhật ký, 1 màn show detail của 1 nhật ký
- chức năng backup/ restore: khi backup tất cả dữ liêu sẽ được ghi ra 1 file csv, khi restore tất cả dữ liệu trong file được import vào app(xóa dữ liệu cũ đi)