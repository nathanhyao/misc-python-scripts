async function fetchData() {
    const res = await fetch("https://api.coronavirus.data.gov.uk/v1/data")
    const record = await res.json()
}
fetchData()