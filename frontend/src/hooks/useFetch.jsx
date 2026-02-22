// import { useState, useEffect } from "react";

// export default function useFetch(apiFunc, params = []) {
//   const [data, setData] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   const fetchData = async (...args) => {
//     setLoading(true);
//     setError(null);
//     try {
//       const result = await apiFunc(...args);
//       setData(result);
//     } catch (err) {
//       setError(err);
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     fetchData(...params);
//   }, params);

//   return { data, loading, error, refetch: fetchData };
// }
