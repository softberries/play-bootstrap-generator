$ ->
  $.get "/personsjs", (persons) ->
    $.each persons, (index, person) ->
      id = $("<div>").addClass("id").text person.id
      name = $("<div>").addClass("name").text person.name
      age = $("<div>").addClass("age").text person.age
      $("#persons").append $("<li>").append(name).append(age).append(id)