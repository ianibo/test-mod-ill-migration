echo
echo -n "Code Coverage: "
grep -oP '(?si)id="coveragetable".*?tfoot.*?>\s*<tr.*?>(\s*<td.*?>.*?<\/td>){2}<td.*?>.*?\K\d+(?=%.*?<\/td>)' build/jacocoHtml/index.html
echo
