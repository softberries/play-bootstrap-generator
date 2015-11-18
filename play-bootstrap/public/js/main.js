$('#my-ajax-table').dynatable({
    dataset: {
        ajax: true,
        ajaxUrl: '/personsjs',
        ajaxData: this.queries,
        ajaxOnLoad: true,
        records: []
    },
    writers: {
        _rowWriter: function (rowIndex, record, columns, cellWriter) {
            return '<tr><td>'+record.id+'</td><td>'+record.name+'</td><td>'+record.age+'</td><td><a href="/person/'+record.id+'">Edit</a></td></tr>';
        }
    },
    readers: {
        _rowReader: null
    }
});
