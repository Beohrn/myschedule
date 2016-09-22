package app.voter.xyz.comments

class Comment {
  var text = ""
  var user_id = ""
  var datetime = ""
  var likes = hashMapOf<String, String>()
  var replies = hashMapOf<String, Comment>()

  constructor() {
  }

  constructor(userId: String, text: String, datetime: String) {
    this.text = text
    this.user_id = userId
    this.datetime = datetime
  }

  fun isLikedByUser(userId: String) = likes.filterValues { it.equals(userId) }.keys.firstOrNull()
}