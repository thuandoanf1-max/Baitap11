<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Chi tiết sách</title>
</head>
<body>
<a class="back-link" href="${pageContext.request.contextPath}/home">← Quay về trang chủ</a>

<c:choose>
    <c:when test="${not empty detailError}">
        <section class="detail-error-panel">
            <span class="eyebrow">BOOK DETAIL</span>
            <h1>Không thể hiển thị sách</h1>
            <p><c:out value="${detailError}"/></p>
            <a class="button" href="${pageContext.request.contextPath}/home">Xem danh sách sách</a>
        </section>
    </c:when>

    <c:otherwise>
        <c:url var="coverUrl" value="/assets/images/${book.coverImage}"/>
        <c:url var="fallbackCoverUrl" value="/assets/images/book-placeholder.svg"/>

        <section class="book-detail">
            <div class="detail-cover">
                <c:choose>
                    <c:when test="${not empty book.coverImage}">
                        <img src="${coverUrl}"
                             alt="Bìa sách ${book.title}"
                             onerror="this.onerror=null;this.src='${fallbackCoverUrl}';">
                    </c:when>
                    <c:otherwise>
                        <img src="${fallbackCoverUrl}" alt="Sách chưa có ảnh bìa">
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="detail-information">
                <span class="eyebrow">BOOK DETAIL • CÂU 4</span>
                <h1><c:out value="${book.title}"/></h1>
                <dl class="detail-facts">
                    <div>
                        <dt>Mã ISBN</dt>
                        <dd><c:out value="${book.isbn}" default="Chưa cập nhật"/></dd>
                    </div>
                    <div>
                        <dt>Tác giả</dt>
                        <dd><c:out value="${book.authorNames}" default="Chưa xác định"/></dd>
                    </div>
                    <div>
                        <dt>Publisher</dt>
                        <dd><c:out value="${book.publisher}" default="Chưa cập nhật"/></dd>
                    </div>
                    <div>
                        <dt>Publish date</dt>
                        <dd><c:out value="${book.publishDate}" default="Chưa cập nhật"/></dd>
                    </div>
                    <div>
                        <dt>Quantity</dt>
                        <dd><c:out value="${book.quantity}" default="0"/></dd>
                    </div>
                    <div>
                        <dt>Reviews</dt>
                        <dd class="review-count">Reviews (${book.reviewCount})</dd>
                    </div>
                </dl>

                <%-- Nút thêm vào giỏ hàng --%>
                <c:choose>
                    <c:when test="${empty sessionScope.currentUser}">
                        <div style="margin-top:28px;">
                            <a class="button"
                               href="${pageContext.request.contextPath}/login?required=cart">
                                🛒 Đăng nhập để thêm vào giỏ
                            </a>
                        </div>
                    </c:when>
                    <c:when test="${empty book.quantity or book.quantity <= 0}">
                        <div style="margin-top:28px;">
                            <span style="display:inline-block;padding:12px 22px;border-radius:8px;
                                         background:#eceae4;color:#9a9a96;font-weight:700;cursor:not-allowed;">
                                Hết hàng
                            </span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/cart"
                              style="margin-top:28px;display:flex;align-items:center;gap:10px;flex-wrap:wrap;">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="bookId" value="${book.bookId}">
                            <div style="display:flex;flex-direction:column;gap:4px;">
                                <label for="addQty"
                                       style="font-size:0.82rem;font-weight:700;color:var(--muted);">
                                    Số lượng (tối đa ${book.quantity})
                                </label>
                                <input id="addQty" type="number" name="quantity"
                                       value="1" min="1" max="${book.quantity}" required
                                       style="width:80px;padding:10px 12px;border:1px solid #c9c6be;
                                              border-radius:7px;font:inherit;text-align:center;">
                            </div>
                            <button class="button" type="submit" style="margin-top:20px;">
                                🛒 Thêm vào giỏ hàng
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>

            </div>
        </section>

        <section class="reviews-section">
            <div class="section-title-row">
                <div>
                    <span class="eyebrow">Ý KIẾN ĐỘC GIẢ</span>
                    <h2>Reviews</h2>
                </div>
                <span class="result-count">${book.reviewCount} review</span>
            </div>

            <c:if test="${not empty reviewSuccess}">
                <div class="alert alert-success"><c:out value="${reviewSuccess}"/></div>
            </c:if>
            <c:if test="${not empty reviewError}">
                <div class="alert alert-error"><c:out value="${reviewError}"/></div>
            </c:if>

            <div class="review-list">
                <c:forEach items="${reviews}" var="review">
                    <article class="review-item">
                        <div class="review-user">
                            <strong><c:out value="${review.reviewerName}"/></strong>
                            <span>${review.rating}/5 ★</span>
                        </div>
                        <p><c:out value="${review.reviewText}"/></p>
                    </article>
                </c:forEach>
            </div>

            <c:if test="${empty reviews}">
                <div class="empty-state">Cuốn sách này chưa có review.</div>
            </c:if>
        </section>

        <section class="review-form-section">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <div class="section-title-row">
                        <div>
                            <span class="eyebrow">ĐÁNH GIÁ SÁCH</span>
                            <h2>Thêm review</h2>
                        </div>
                        <span class="reviewing-as">Đăng với tên <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong></span>
                    </div>

                    <form class="review-form" method="post"
                          action="${pageContext.request.contextPath}/book/detail">
                        <input type="hidden" name="bookId" value="${book.bookId}">

                        <label for="rating">Điểm đánh giá</label>
                        <select id="rating" name="rating" required>
                            <option value="">-- Chọn điểm --</option>
                            <option value="5">5 - Rất hay</option>
                            <option value="4">4 - Hay</option>
                            <option value="3">3 - Bình thường</option>
                            <option value="2">2 - Chưa tốt</option>
                            <option value="1">1 - Không phù hợp</option>
                        </select>

                        <label for="reviewText">Nội dung review</label>
                        <textarea id="reviewText" name="reviewText" rows="6" maxlength="2000"
                                  placeholder="Chia sẻ cảm nhận của bạn về cuốn sách..." required></textarea>

                        <button class="button" type="submit">Gửi review</button>
                    </form>
                </c:when>

                <c:otherwise>
                    <div class="login-to-review">
                        <h2>Đăng nhập để thêm review</h2>
                        <p>Bạn cần đăng nhập trước khi gửi đánh giá cho cuốn sách này.</p>
                        <a class="button" href="${pageContext.request.contextPath}/login?required=review">Đăng nhập</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </c:otherwise>
</c:choose>
</body>
</html>
