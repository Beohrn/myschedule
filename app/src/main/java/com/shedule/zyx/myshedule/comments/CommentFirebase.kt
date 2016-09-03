package app.voter.xyz.comments

class CommentFirebase {
  var text = ""
  var user_id = ""
  var datetime = ""
  var likes = hashMapOf<String, String>()
  var replies = hashMapOf<String, CommentFirebase>()

  constructor() {
  }

  constructor(userId: String, text: String, datetime: String) {
    this.text = text
    this.user_id = userId
    this.datetime = datetime
  }
}