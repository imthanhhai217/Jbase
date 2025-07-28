package com.juhalion.bae.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

/**
 * Quản lý trạng thái tải (loading state) toàn cục của ứng dụng.
 * Sử dụng một bộ đếm để xử lý nhiều yêu cầu tải đồng thời.
 */
object LoadingStateManager {
    /**
     * Bộ đếm các hoạt động đang tải.
     * Giá trị tăng lên khi một hoạt động tải bắt đầu và giảm khi nó kết thúc.
     */
    private val _loadingCount = MutableStateFlow(0)
    
    /**
     * CoroutineScope được sử dụng cho stateIn để định nghĩa vòng đời của StateFlow.
     * Sử dụng Dispatchers.Main để đảm bảo các cập nhật UI được thực hiện trên luồng chính.
     */
    private val applicationScope = CoroutineScope(Dispatchers.Main) 

    /**
     * StateFlow công khai biểu thị trạng thái tải hiện tại của ứng dụng.
     * Giá trị là 'true' nếu có bất kỳ hoạt động tải nào đang diễn ra (loadingCount > 0),
     * và 'false' nếu không có hoạt động tải nào.
     *
     * - map: Chuyển đổi giá trị Int của _loadingCount thành Boolean.
     * - stateIn: Chuyển đổi Flow thành StateFlow, cung cấp giá trị khởi tạo và quản lý vòng đời.
     *   - scope: Phạm vi Coroutine mà StateFlow sẽ hoạt động.
     *   - started: Chiến lược bắt đầu và dừng của StateFlow.
     *     - SharingStarted.WhileSubscribed(5000): Bắt đầu chia sẻ khi có người đăng ký
     *       và dừng sau 5 giây nếu không còn người đăng ký nào.
     *   - initialValue: Giá trị ban đầu của StateFlow.
     */
    val isLoading = _loadingCount
        .map { count -> count > 0 }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Tăng bộ đếm tải lên 1.
     * Hàm này nên được gọi khi một hoạt động tải mới bắt đầu (ví dụ: trước một API call).
     */
    fun showLoading() {
        _loadingCount.value++
    }

    /**
     * Giảm bộ đếm tải xuống 1.
     * Hàm này nên được gọi khi một hoạt động tải kết thúc (thành công hoặc thất bại).
     * Đảm bảo rằng bộ đếm không giảm xuống dưới 0.
     */
    fun hideLoading() {
        if (_loadingCount.value > 0) { 
            _loadingCount.value--
        }
    }
}
